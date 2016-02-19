package lamp.agent.genie.spring.boot.base.exception;

import lamp.agent.genie.core.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ApiError handleException(Exception ex) {
		log.warn("handleException", ex);
		ApiError error = new ApiError();
		error.setCode(ErrorCode.INTERNAL.name());
		error.setMessage(ErrorCode.INTERNAL.getDefaultMessage());
		return error;
	}


	@ExceptionHandler(AppException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ApiError handleException(AppException ex) {
		log.warn("handleAppException", ex);
		ApiError error = new ApiError();
		error.setCode(ex.getCode());
		error.setMessage(ex.getMessage());
		return error;
	}

	@ExceptionHandler(MessageException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ApiError handleException(MessageException ex) {
		log.warn("handleMessageException", ex);
		ApiError error = new ApiError();
		error.setCode(ex.getCode());
		error.setMessage(ex.getMessage());
		return error;
	}

}
