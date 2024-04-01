package life.cookedfox.bookmarkenhance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class BookmarkEnhanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookmarkEnhanceApplication.class, args);
	}

	@Bean
	public RestClient restClient() {
		return RestClient.create();
	}
}
