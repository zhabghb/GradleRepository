package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.chinacloud.oneaa.common.entity.Role;
import com.chinacloud.oneaa.common.entity.UserInfo;
import com.chinacloud.oneaa.common.entity.UserRole;
import com.chinacloud.oneaa.common.exception.DuplicatedEntityException;
import com.chinacloud.oneaa.common.service.OneAAService;
import com.chinacloud.oneaa.common.service.OneAAServiceImpl;
import com.chinacloud.oneaa.common.service.UserPrivService;
import com.chinacloud.oneaa.common.service.UserPrivServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@ComponentScan(basePackages = {"com.chinacloud.oneaa.common.service"})
@Controller
public class OneAAController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String MODUEL_NAME = "metagrid";
    private final static String STATUS_OK = "ok";
    private final static String STATUS_ERROR = "error";


    OneAAService oneAAService = new OneAAServiceImpl();

    UserPrivService userPrivService = new UserPrivServiceImpl();

    @Autowired
    protected HttpServletRequest request;
    @Value("${metagrid.backend.host}")
    private String backendHost;


    @RequestMapping(value = "/healthCheck", method = RequestMethod.GET)
    @ResponseBody
    public Map healthCheck() {
        Map<String, Object> returnMap = new HashMap<String, Object>();

        try {
            String[] ipAport = backendHost.split("://")[1].split("\\:");
            Socket backendSocket = new Socket(ipAport[0], Integer.parseInt(ipAport[1]));
            returnMap.put("service", MODUEL_NAME);
            returnMap.put("status", STATUS_OK);
            return returnMap;
        } catch (IOException e) {
            logger.warn("backend-service :{} 连接失败",backendHost);
            e.printStackTrace();
            throw new AppException("backend-service服务异常",ErrorCode.STATUS_ERROR);
        }

    }

    @RequestMapping(value = "/tokenToUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public Map tokenToUserInfo2() {
        String token = request.getHeader("X-Auth-Token");

        Map<String, Object> returnMap = new HashMap<String, Object>();
        UserInfo userInfo = null;

        userInfo = oneAAService.isValidateToken(token);

        if(userInfo == null){
            throw new AppException("token is not valid!", ErrorCode.UNAUTHORIZED);
        }

        returnMap.put("userInfo", userInfo);
        return returnMap;
    }

    /*oneaa清除脏数据的方式由之前的客户端定时任务改为服务端主动调用此接口*/
    @RequestMapping(value = "/projects/{projectId}/users", method = RequestMethod.PUT)
    @ResponseBody
    public void refreshAllTheUsers(@PathVariable String projectId,@RequestBody Map map) {
        List<String> userIDList = (List<String>) map.get("userIds");
        List<UserRole> userRoleList = userPrivService.getUserRoleList();
        for(UserRole userRole : userRoleList){
            String userID = userRole.getUserId();
            if(!userIDList.contains(userID)){
                userPrivService.deleteUserRole(projectId, userID);
                logger.info("refresh tenentId: " + projectId + " userId: " + userID);
            }
        }
    }


    public static String beanToJson(Object obj) {
        Gson gson = new Gson();
        return jsonFormatter(gson.toJson(obj));
    }

    public static String jsonFormatter(String uglyJSONString) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJSONString);
        String prettyJsonString = gson.toJson(je);
        return prettyJsonString;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/priv/v1/roles"},
            method = {RequestMethod.POST}
    )
    public ResponseEntity addRole(HttpServletRequest request, @RequestBody Role role) {
        logger.info("request[addRole]==" + beanToJson(role));
        Role rerole = null;

        try {
            rerole = this.userPrivService.addRole(role);
            logger.info("创建", "角色,roleName[" + role.getRoleName() + "]", "成功");
        } catch (DuplicatedEntityException var5) {
            logger.info("创建", "角色,roleName[" + role.getRoleName() + "]", "失败,错误消息：角色名[" + role.getRoleName() + "]已经存在");
            return ResponseEntity.status(409).contentType(MediaType.APPLICATION_JSON).body("角色名[" + role.getRoleName() + "]已经存在.");
        }

        logger.debug("response[addRole]==" + beanToJson(rerole));
        return ResponseEntity.ok(rerole);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/priv/v1/roles/{roleId}"},
            method = {RequestMethod.DELETE}
    )
    public ResponseEntity deleteRole(HttpServletRequest request, @PathVariable String roleId) {
        try {
            this.userPrivService.removeRole(roleId);
            logger.info("删除", "角色,roleId[" + roleId + "]", "成功");
        } catch (DuplicatedEntityException var4) {
            logger.info("删除", "角色,roleId[" + roleId + "]", "失败,错误消息：角色已经关联了用户，不能删除");
            return ResponseEntity.status(409).contentType(MediaType.APPLICATION_JSON).body("角色已经关联了用户，不能删除.");
        }

        return ResponseEntity.ok("删除角色成功");
    }

    @ResponseBody
    @RequestMapping(
            value = {"/priv/v1/roles/{roleId}"},
            method = {RequestMethod.PUT}
    )
    public Role updateRole(@PathVariable String roleId, @RequestBody Role role) {
        logger.info("修改", "角色,roleId[" + roleId + "]");
        role.setRoleId(roleId);
        return this.userPrivService.updateRole(role);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/priv/v1/roles"},
            method = {RequestMethod.GET}
    )
    public Map<String, List<Role>> findRoles() {
        HashMap returnMap = new HashMap();
        List list = this.userPrivService.getRoles("");
        if (list != null) {
            returnMap.put("roles", list);
        } else {
            returnMap.put("roles", new ArrayList());
        }

        logger.debug("response[findRoles]==" + beanToJson(returnMap));
        return returnMap;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/priv/v1/users/{userId}/tenant/{tenantId}/roles"},
            method = {RequestMethod.GET}
    )
    public Map<String, List<String>> findRolesForUserId(@PathVariable String userId, @PathVariable String tenantId) {
        HashMap returnMap = new HashMap();
        List list = this.userPrivService.getRolesByUserIdAndTenantId(userId, tenantId);
        if (list != null) {
            returnMap.put("roleIds", list);
        } else {
            returnMap.put("roleIds", new ArrayList());
        }

        logger.debug("response[findRolesForUserId]==" + beanToJson(returnMap));
        return returnMap;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/priv/v1/roles/{roleId}/users"},
            method = {RequestMethod.GET}
    )
    public Map<String, List<String>> findUsersForRoleId(@PathVariable String roleId) {
        HashMap returnMap = new HashMap();
        List list = this.userPrivService.getUsersByRoleId(roleId);
        if (list != null) {
            returnMap.put("userIds", list);
        } else {
            returnMap.put("userIds", new ArrayList());
        }

        logger.debug("response[findUsersForRoleId]==" + beanToJson(returnMap));
        return returnMap;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/priv/v1/roles/{roleId}/privs"},
            method = {RequestMethod.PUT}
    )
    public Map<String, List<String>> addPrivsForRole(@PathVariable String roleId, @RequestBody Map reqMap) {
        HashMap returnMap = new HashMap();
        logger.info("设置", "角色权限,roleId[" + roleId + "]");
        logger.debug("request[addPrivsForRole]==" + beanToJson(reqMap));
        List list = this.userPrivService.addPrivsByRoleId(roleId, (List) reqMap.get("privs"));
        if (list != null) {
            returnMap.put("privs", list);
        } else {
            returnMap.put("privs", new ArrayList());
        }

        logger.debug("response[addPrivsForRole]==" + beanToJson(returnMap));
        return returnMap;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/priv/v1/users/{userId}/roles"},
            method = {RequestMethod.PUT}
    )
    public Map addRolesForUser(@PathVariable String userId, @RequestBody Map map) {
        HashMap returnMap = new HashMap();
        logger.info("设置", "用户角色,userId[" + userId + "]");
        logger.debug("request[addRolesForUser]==" + beanToJson(map));
        String tenantId = String.valueOf(map.get("tenantId"));
        List roleIds = (List) map.get("roleIds");
        List list = this.userPrivService.addRolesByUserId(userId, tenantId, roleIds);
        returnMap.put("roleIds", list);
        returnMap.put("tenantId", tenantId);
        logger.debug("response[addRolesForUser]==" + beanToJson(returnMap));
        return returnMap;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/priv/v1/roles/{roleId}/privs"},
            method = {RequestMethod.GET}
    )
    public Map findPrivsForRole(@PathVariable String roleId) {
        HashMap returnMap = new HashMap();
        List list = this.userPrivService.getPrivsByRoleId(roleId);
        returnMap.put("privs", list);
        logger.debug("response[findPrivsForRole]==" + beanToJson(returnMap));
        return returnMap;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/priv/v1/privs"},
            method = {RequestMethod.GET}
    )
    public Map findAllPrivs() {
        List list = this.userPrivService.getAllPrivs();
        HashMap returnMap = new HashMap();
        returnMap.put("privs", list);
        logger.debug("response[findAllPrivs]==" + beanToJson(returnMap));
        return returnMap;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/priv/v1/users/{userId}/privs"},
            method = {RequestMethod.GET}
    )
    public Map findPrivsForUser (@PathVariable String userId) {
        List listPrivs = this.userPrivService.getPrivsByUserIdGroupTenantId(userId);
        HashMap returnMap = new HashMap();
        returnMap.put("privs", listPrivs);
        logger.debug("response[findPrivsForUser]==" + beanToJson(returnMap));
        return returnMap;
    }

    @ResponseBody
    @RequestMapping(value = "/priv/v1/users/{userId}/rolenames", method = {RequestMethod.GET})
    public Map findRoleNamesForUserId(@PathVariable String userId) {
        Map<String, List<String>> returnMap = new HashMap<String, List<String>>();
        List<Role> roleList = new ArrayList<Role>();
        List<String> roleNameList = new ArrayList<String>();
        List<UserRole> userRoleList = userPrivService.getRolesAndTenantsByUserId(userId);

        for (UserRole ur : userRoleList) {
            roleList.addAll(userPrivService.getRoles(ur.getRoleId()));
        }
        for (Role role : roleList) {
            roleNameList.add(role.getRoleName());
        }
        returnMap.put("rolenames", roleNameList);

        return returnMap;

    }

}
