package it.smartcommunitylab.climb.domain.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.model.gamification.PedibusGameConfTemplate;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Controller
public class GameConfController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(GameConfController.class);
	
	@Autowired
	private RepositoryManager storage;

	@RequestMapping(value = "/api/game/conf/template", method = RequestMethod.GET)
	public @ResponseBody List<PedibusGameConfTemplate> getConfTemplates(
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		List<PedibusGameConfTemplate> result = storage.getPedibusGameConfTemplates();
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getConfTemplates: %s", result.size()));
		}
		return result; 
	}
	
	@RequestMapping(value = "/api/game/conf/template", method = RequestMethod.POST)
	public @ResponseBody PedibusGameConfTemplate saveConfTemplate(
			@RequestBody PedibusGameConfTemplate confTemplate,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		PedibusGameConfTemplate result = storage.savePedibusGameConfTemplate(confTemplate);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveConfTemplate: %s - %s", result.getName(), result.getVersion()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/conf/{ownerId}/{pedibusGameId}/template/{templateId}", method = RequestMethod.PUT)
	public @ResponseBody void setConfTemplate(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String templateId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		PedibusGameConfTemplate confTemplate = storage.getPedibusGameConfTemplate(templateId);
		if(confTemplate == null) {
			throw new EntityNotFoundException("game conf template not found");
		}
		game.setConfTemplateId(templateId);
		storage.updatePedibusGameConfTemplateId(ownerId, pedibusGameId, templateId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("setConfTemplate: %s - %s", ownerId, pedibusGameId));
		}
	}
	
	@RequestMapping(value = "/api/game/conf/{ownerId}/{pedibusGameId}/params", method = RequestMethod.PUT)
	public @ResponseBody void updateConfParams(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@RequestBody Map<String, String> params, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.updatePedibusGameConfParams(ownerId, pedibusGameId, params);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateConfParams: %s - %s", ownerId, pedibusGameId));
		}
	}	
	
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Map<String,String> handleEntityNotFoundError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	@ResponseBody
	public Map<String,String> handleUnauthorizedError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleGenericError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
}
