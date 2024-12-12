package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.api.dtos.flag.PreFlag;
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
        operator.addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_CreateTemplateSuccess, templateName);
        operator.addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_CreateTemplateFailed, templateName);
        if (templateName.contains(" ")) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_TemplateNameInvalid);
            return;
        }
        List<PrivilegeTemplateDTO> templates = PrivilegeTemplateDTO.selectAll(operator.getUniqueId());
        for (PrivilegeTemplateDTO template : templates) {
            if (template.getName().equals(templateName)) {
                operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_TemplateNameExist, templateName);
                return;
            }
        }
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.create(operator.getUniqueId(), templateName);
        if (template == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            return;
        }
        operator.completeResult();
    }

    /**
     * 删除权限模板
     *
     * @param operator     操作者
     * @param templateName 模板名称
     */
    public static void deleteTemplate(AbstractOperator operator, String templateName) {
        operator.addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_DeleteTemplateSuccess, templateName);
        operator.addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_DeleteTemplateFailed, templateName);
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(operator.getUniqueId(), templateName);
        if (template == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_TemplateNotExist, templateName);
            return;
        }
        if (!template.getCreator().equals(operator.getUniqueId())) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_TemplateNotExist, templateName);
            return;
        }
        PrivilegeTemplateDTO.delete(operator.getUniqueId(), templateName);
        operator.completeResult();
    }

    /**
     * 设置权限模板的权限
     *
     * @param operator     操作者
     * @param templateName 模板名称
     * @param flag         权限
     * @param value        权限值
     */
    public static void setTemplateFlag(AbstractOperator operator, String templateName, PreFlag flag, boolean value) {
        operator.addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_SetTemplateFlagSuccess, templateName, flag.getDisplayName(), value);
        operator.addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_SetTemplateFlagFailed, templateName, flag.getDisplayName(), value);
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(operator.getUniqueId(), templateName);
        if (template == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_TemplateNotExist, templateName);
            return;
        }
        if (flag.getFlagName().equals("admin")) {
            template.setAdmin(value);
        } else {
            template.setFlagValue(flag, value);
        }
        operator.completeResult();
    }

}
