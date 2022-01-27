

# spring-cloud

# 애플리케이션 구성 요소 
```
eureka-server: 마이크로서비스 등록 및 검색 
gateway: 마이크로서비스 부하 분산 및 라우팅
 
member: 멤버 마이크로 서비스  
member-load-balance: 멤버 로드밸런싱 테스트 마이크로 서비스  
product: 상품 마이크로 서비스  
```


# Spring Cloud Gateway
- `비동기 통신`을 지원하므로 Tomcat 대신 `Netty` 서버가 뜬다.

## Spring cloud gateway 적용 
### application.yml 
- ```
  spring:
    cloud:
      gateway:
        routes:
          - id: member
            uri: http://localhost:8081/
            predicates:
              - Path=/member/**
            filters:
            - AddRequestHeader=member-request, member-request2
            - AddResponseHeader=member-response, member-reponse2
  ```
- 게이트웨이에서 서비스 호출시 `http://localhost:8081/member/**` 와 같은 형태로 호출되니, 컨트롤러에 @RequestMapping("/member")와 같은 member 경로가 필요하므로 주의해야함.
- filters로 request, response 헤더값을 주거나 custom한 filter를 적용할 수 있다.  


### RouteLocatorBuilder 
- ```java
  @Configuration
  public class FilterConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/member/**")
                            .filters(f -> f.addRequestHeader("member-request", "member-request")
                                            .addResponseHeader("member-response", "member-response"))
                            .uri("http://localhost:8081"))
                .build();
    }
  }
  ```
- application.yml 대신 `자바 코드`로 설정할 수 있다. 
- 위 application.yml에서 헤더 설정한 것처럼 `/member/**`로 요청시 request, response에 헤더를 추가했다. 

## Custom Filter 적용 
- ```
  @Component
  @Slf4j
  public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom PRE Filter: request id : {}", request.getId());

            // Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom POST Filter: response code : {}", response.getStatusCode());
            }));
        });
    }


    public static class Config {
        // Put the configuration properties
    }
  }     
  ```
- ```
  spring:
    cloud:
      gateway:
        routes:
          - id: member
            uri: http://localhost:8081/
            predicates:
              - Path=/member/**
            filters:
            //- AddRequestHeader=member-request, member-request2
            //- AddResponseHeader=member-response, member-response2
             - name: CustomFilter
  ```
- CustomFilter를 적용하고싶은 서비스에 필터를 추가해준다. application.yml에 헤더 대신 필터를 추가함. 
 
## Global Filter 적용 
- ```
  @Component
  @Slf4j
  public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global Filter baseMessage: {}", config.getBaseMessage());

            if(config.isPreLogger()) {
                log.info("Global Filter Start Request id : {} ", request.getId());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.isPostLogger()) {
                    log.info("Global Filter End Response code : {} ", response.getStatusCode());
                }
            }));
        });
    }


    @Data
    public static class Config {
        // Put the configuration properties
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
  }  
  ```
- Config 클래스에 추가한 변수를 `apply(Config config)` 메서드에서 활용할 수 있다.
- ```
  spring:
    cloud:
      gateway:
        default-filters:
          - name: GlobalFilter
            args:
              baseMessage: Spring CLoud Gateway Global Filter
              preLogger: true
              postLogger: true
  ```
- 글로벌 필터는 default-filters에 추가해주면 된다. 
- Config 클래스 변수값은 args로 넣어줄 수 있다. 


## eureka-server 적용 
- eureka 서버에 유레카 클라이언트로 등록되면 어플리케이션 이름으로 라우팅을 할 수 있다.
  + `uri: http://localhost:8081` -> `uri: lb://MEMBER`
- ```
  spring:
    cloud:
      gateway:
        routes:
          - id: member
            uri: lb://MEMBER
            ...
  ```

## spring-cloud load balance 
- 스프링 클라우드 로드 밸런싱 테스트를 위해 member-load-balance 모듈 추가. 
- 다른 설정 사항은 동일하고 포트만 `8081`, `9081`로 다름. 
- 어플리케이션 이름이 동일하고, 유레카 서버 설정에 `uri: lb://MEMBER`로 등록해 뒀으므로 추가로 설정할건 없다. 
- [spring-cloud-gateway-load-balance](/images/spring-cloud-gateway-load-balance.png)
- eureka-server, gateway, member, member-load-balance를 구동시키면 유레카 대시보드에 다음과 같이 나온다.
- [member_lb](/images/member_lb.png)
- [member_load_balance_lb](/images/member_load_balance_lb.png)
- `http://localhost:8000/member/load-balance` 로 `게이트웨이`를 통해 api를 호출해보면, `라운드로빈 방식`으로 `member`, `member-load-balance`가 번갈아 호출된다.
- 즉, 클라이언트 api 호출 -> 게이트웨이 -> 유레카 서버(서비스 조회) -> 라운드 로빈 방식으로 서비스 호출  