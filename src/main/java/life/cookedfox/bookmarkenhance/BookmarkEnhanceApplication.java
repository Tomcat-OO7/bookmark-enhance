package life.cookedfox.bookmarkenhance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@EnableScheduling
public class BookmarkEnhanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookmarkEnhanceApplication.class, args);
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10 * 1000);
        factory.setReadTimeout(2 * 60 * 1000);
        return factory;
    }

    @Bean
    public RestClient restClient(ClientHttpRequestFactory clientHttpRequestFactory) {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory)
                .build();
    }
}
