package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.dominion.managers.Translation;

import java.util.List;

public class TemplateController {

    /**
     * 创建权限模板
     *
     * @param operator     操作者
     * @param templateName 模板名称
     */
    public static void createTemplate(AbstractOperator operator, String templateName) {
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_CreateTemplateSuccess, templateName);
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_CreateTemplateFailed, templateName);
        if (templateName.contains(" ")) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_TemplateNameInvalid));
            return;
        }
        List<PrivilegeTemplateDTO> templates = PrivilegeTemplateDTO.selectAll(operator.getUniqueId());
        for (PrivilegeTemplateDTO template : templates) {
            if (template.getName().equals(templateName)) {
                operator.setResponse(FAIL.addMessage(Translation.Messages_TemplateNameExist, templateName));
                return;
            }
        }
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.create(operator.getUniqueId(), templateName);
        if (template == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DatabaseError));
            return;
        }
        operator.setResponse(SUCCESS);
    }

    /**
     * 删除权限模板
     *
     * @param operator     操作者
     * @param templateName 模板名称
     */
    public static void deleteTemplate(AbstractOperator operator, String templateName) {
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_DeleteTemplateSuccess, templateName);
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_DeleteTemplateFailed, templateName);
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(operator.getUniqueId(), templateName);
        if (template == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_TemplateNotExist, templateName));
            return;
        }
        if (!template.getCreator().equals(operator.getUniqueId())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_TemplateNotExist, templateName));
            return;
        }
        PrivilegeTemplateDTO.delete(operator.getUniqueId(), templateName);
        operator.setResponse(SUCCESS);
    }

    /**
     * 设置权限模板的权限
     *
     * @param operator     操作者
     * @param templateName 模板名称
     * @param flag_name    权限名称
     * @param value        权限值
     */
    public static void setTemplateFlag(AbstractOperator operator, String templateName, String flag_name, boolean value) {
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_SetTemplateFlagSuccess, templateName, flag_name, value);
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_SetTemplateFlagFailed, templateName, flag_name, value);
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(operator.getUniqueId(), templateName);
        if (template == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_TemplateNotExist, templateName));
            return;
        }
        if (flag_name.equals("admin")) {
            template.setAdmin(value);
        } else {
            Flag f = Flag.getFlag(flag_name);
            if (f == null) {
                operator.setResponse(FAIL.addMessage(Translation.Messages_UnknownFlag, flag_name));
                return;
            }
            template.setFlagValue(f, value);
        }
        operator.setResponse(SUCCESS);
    }

}
