package com.mymooc.content.feignclient;

import com.mymooc.search.po.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "search", fallbackFactory = SearchServiceClientFallbackFactory.class)
public interface SearchServiceClient {

	@PostMapping("/search/index/course")
	public Boolean add(@RequestBody CourseIndex courseIndex);
}
