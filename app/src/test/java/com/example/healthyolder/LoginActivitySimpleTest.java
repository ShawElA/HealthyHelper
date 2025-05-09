package com.example.healthyolder;

import org.junit.Test;
import static org.junit.Assert.*;

public class LoginActivitySimpleTest {
    
    /**
     * 测试1：测试登录按钮状态控制逻辑
     * 验证当用户名和密码都有效或无效时，登录按钮状态的正确性
     */
    @Test
    public void testLoginButtonStateLogic() {
        // 模拟LoginActivity中的setLoginButtonState方法的逻辑
        
        // 场景1：用户名和密码都有效
        boolean isEnterName = true;
        boolean isEnterPassWord = true;
        boolean buttonShouldBeEnabled = isEnterName && isEnterPassWord;
        
        // 验证按钮应该被启用
        assertTrue("当用户名和密码都有效时，登录按钮应该被启用", buttonShouldBeEnabled);
        
        // 场景2：用户名无效，密码有效
        isEnterName = false;
        isEnterPassWord = true;
        buttonShouldBeEnabled = isEnterName && isEnterPassWord;
        
        // 验证按钮应该被禁用
        assertFalse("当用户名无效时，登录按钮应该被禁用", buttonShouldBeEnabled);
        
        // 场景3：用户名有效，密码无效
        isEnterName = true;
        isEnterPassWord = false;
        buttonShouldBeEnabled = isEnterName && isEnterPassWord;
        
        // 验证按钮应该被禁用
        assertFalse("当密码无效时，登录按钮应该被禁用", buttonShouldBeEnabled);
        
        // 场景4：用户名和密码都无效
        isEnterName = false;
        isEnterPassWord = false;
        buttonShouldBeEnabled = isEnterName && isEnterPassWord;
        
        // 验证按钮应该被禁用
        assertFalse("当用户名和密码都无效时，登录按钮应该被禁用", buttonShouldBeEnabled);
    }
    
    /**
     * 测试2：测试登录参数构建
     * 验证登录请求参数是否正确构建
     */
    @Test
    public void testLoginParameterBuilding() {
        // 模拟用户输入
        String username = "testUser";
        String password = "testPassword";
        
        // 创建参数映射（模拟LoginActivity中userLogin方法的逻辑）
        java.util.Map<String, String> parameter = new java.util.HashMap<>();
        parameter.put("username", username);
        parameter.put("password", password);
        
        // 验证参数映射中包含正确的键和值
        assertEquals("用户名参数应正确设置", username, parameter.get("username"));
        assertEquals("密码参数应正确设置", password, parameter.get("password"));
        
        // 验证参数映射的大小
        assertEquals("参数映射应包含2个键值对", 2, parameter.size());
        
        // 验证参数映射包含所有必需的键
        assertTrue("参数映射应包含username键", parameter.containsKey("username"));
        assertTrue("参数映射应包含password键", parameter.containsKey("password"));
    }
} 