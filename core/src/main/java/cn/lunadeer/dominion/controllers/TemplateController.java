package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;

import java.util.List;

public class TemplateController {

    public static void createTemplate(AbstractOperator operator, String templateName) {
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "创建成功");
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "创建失败");
        if (templateName.contains(" ")) {
            operator.setResponse(FAIL.addMessage("模板名称不能包含空格"));
            return;
        }
        List<PrivilegeTemplateDTO> templates = PrivilegeTemplateDTO.selectAll(operator.getUniqueId());
        for (PrivilegeTemplateDTO template : templates) {
            if (template.getName().equals(templateName)) {
                operator.setResponse(FAIL.addMessage("已经存在名为 %s 的权限模板", templateName));
                return;
            }
        }
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.create(operator.getUniqueId(), templateName);
        if (template == null) {
            operator.setResponse(FAIL.addMessage("可能是数据库错误，请联系管理员"));
            return;
        }
        operator.setResponse(SUCCESS.addMessage("成功创建名为 %s 的权限模板", templateName));
    }

    public static void deleteTemplate(AbstractOperator operator, String templateName) {
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "删除成功");
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "删除失败");
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(operator.getUniqueId(), templateName);
        if (template == null) {
            operator.setResponse(FAIL.addMessage("模板不存在"));
            return;
        }
        if (!template.getCreator().equals(operator.getUniqueId())) {
            operator.setResponse(FAIL.addMessage("这不是你的模板"));
            return;
        }
        PrivilegeTemplateDTO.delete(operator.getUniqueId(), templateName);
        operator.setResponse(SUCCESS);
    }

    public static void setTemplateFlag(AbstractOperator operator, String templateName, String flag_name, boolean value) {
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "设置成功");
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "设置失败");
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(operator.getUniqueId(), templateName);
        if (template == null) {
            operator.setResponse(FAIL.addMessage("模板不存在"));
            return;
        }
        if (flag_name.equals("admin")) {
            template.setAdmin(value);
        } else {
            Flag f = Flag.getFlag(flag_name);
            if (f == null) {
                operator.setResponse(FAIL.addMessage("未知的权限 %s", flag_name));
                return;
            }
            template.setFlagValue(f, value);
        }
        operator.setResponse(SUCCESS.addMessage("成功设置模板 " + template.getName() + " 的权限 " + flag_name + " 为 " + value));
    }

}
