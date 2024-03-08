package com.mymooc.content.feignclient;

import com.mymooc.search.po.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {

	@Override
	public SearchServiceClient create(Throwable throwable) {
		return new SearchServiceClient() {
			@Override
			public Boolean add(CourseIndex courseIndex) {
				log.debug("添加课程索引发生熔断");
				return false;
			}
		};
	}
}
