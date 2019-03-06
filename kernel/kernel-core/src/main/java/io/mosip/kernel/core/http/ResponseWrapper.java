package io.mosip.kernel.core.http;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.mosip.kernel.core.exception.ServiceError;
import lombok.Data;

@Data
public class ResponseWrapper<T> {
	private String id;
	private String version;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime responsetime;
	@NotNull
	@Valid
	private T response;
	
	private List<ServiceError> errors = new ArrayList<>();
	
	private T metadata;
}
