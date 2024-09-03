package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.*;
import cn.lunadeer.dominion.managers.Translation;

import java.util.Objects;

import static cn.lunadeer.dominion.utils.ControllerUtils.noAuthToChangeFlags;
import static cn.lunadeer.dominion.utils.ControllerUtils.notOwner;

public class GroupController {

    /**
     * 创建权限组
     *
     * @param operator    操作者
     * @param domName     领地名称
     * @param groupName   权限组名称
     * @param nameColored 权限组名称（带颜色）
     */
    public static void createGroup(AbstractOperator operator, String domName, String groupName, String nameColored) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_CreateGroupFailed, groupName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_CreateGroupSuccess, groupName);
        if (groupName.contains(" ")) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_GroupNameInvalid));
            return;
        }
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNotExist, domName));
            return;
        }
        if (notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotDominionOwner, domName));
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), groupName);
        if (group != null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_GroupNameExist, domName, groupName));
            return;
        }
        group = GroupDTO.create(nameColored, dominion);
        if (group == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DatabaseError));
            return;
        }
        operator.setResponse(SUCCESS);
    }

    /**
     * 删除权限组
     *
     * @param operator  操作者
     * @param domName   领地名称
     * @param groupName 权限组名称
     */
    public static void deleteGroup(AbstractOperator operator, String domName, String groupName) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_DeleteGroupFailed, groupName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_DeleteGroupSuccess, groupName);
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNotExist, domName));
            return;
        }
        if (notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotDominionOwner, domName));
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), groupName);
        if (group == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_GroupNotExist, domName, groupName));
            return;
        }
        group.delete();
        operator.setResponse(SUCCESS);
    }

    /**
     * 设置权限组权限
     *
     * @param operator  操作者
     * @param domName   领地名称
     * @param groupName 权限组名称
     * @param flag      权限名称
     * @param value     权限值
     */
    public static void setGroupFlag(AbstractOperator operator, String domName, String groupName, String flag, boolean value) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_SetGroupFlagFailed, groupName, flag, value);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_SetGroupFlagSuccess, groupName, flag, value);
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNotExist, domName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) {
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), groupName);
        if (group == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_GroupNotExist, domName, groupName));
            return;
        }
        if ((flag.equals("admin") || group.getAdmin()) && notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotDominionOwnerForGroup, domName));
            return;
        }
        if (flag.equals("admin")) {
            group = group.setAdmin(value);
        } else {
            Flag f = Flag.getFlag(flag);
            if (f == null) {
                operator.setResponse(FAIL.addMessage(Translation.Messages_UnknownFlag, flag));
                return;
            }
            group = group.setFlagValue(f, value);
        }
        if (group == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DatabaseError));
            return;
        }
        operator.setResponse(SUCCESS);
    }

    /**
     * 重命名权限组
     *
     * @param operator    操作者
     * @param domName     领地名称
     * @param oldName     旧名称
     * @param newName     新名称
     * @param nameColored 新名称（带颜色）
     */
    public static void renameGroup(AbstractOperator operator, String domName, String oldName, String newName, String nameColored) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_RenameGroupFailed, oldName, newName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_RenameGroupSuccess, oldName, newName);
        if (newName.contains(" ")) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_GroupNameInvalid));
            return;
        }
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNotExist, domName));
            return;
        }
        if (notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotDominionOwner, domName));
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), oldName);
        if (group == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_GroupNotExist, domName, oldName));
            return;
        }
        group = group.setName(nameColored);
        if (group == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DatabaseError));
            return;
        }
        operator.setResponse(SUCCESS);
    }

    /**
     * 添加成员到权限组
     *
     * @param operator   操作者
     * @param domName    领地名称
     * @param groupName  权限组名称
     * @param playerName 玩家名称
     */
    public static void addMember(AbstractOperator operator, String domName, String groupName, String playerName) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_AddGroupMemberFailed, playerName, groupName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_AddGroupMemberSuccess, playerName, groupName);
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNotExist, domName));
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), groupName);
        if (group == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_GroupNotExist, domName, groupName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NoPermissionForGroupMember, domName, groupName));
            return;
        }
        if (group.getAdmin() && notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotDominionOwnerForGroupMember, domName));
            return;
        }
        PlayerDTO player = PlayerDTO.select(playerName);
        if (player == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotExist, playerName));
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotDominionMember, playerName, domName));
            return;
        }
        if (Objects.equals(privilege.getGroupId(), group.getId())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerAlreadyInGroup, playerName, groupName));
            return;
        }
        if (notOwner(operator, dominion) && privilege.getAdmin()) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerIsOwnerForGroupMember, playerName, domName));
            return;
        }
        privilege = privilege.setGroupId(group.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DatabaseError));
            return;
        }
        operator.setResponse(SUCCESS);
    }

    /**
     * 从权限组移除成员
     *
     * @param operator   操作者
     * @param domName    领地名称
     * @param groupName  权限组名称
     * @param playerName 玩家名称
     */
    public static void removeMember(AbstractOperator operator, String domName, String groupName, String playerName) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_RemoveGroupMemberFailed, groupName, playerName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_RemoveGroupMemberSuccess, groupName, playerName);
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNotExist, domName));
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), groupName);
        if (group == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_GroupNotExist, domName, groupName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NoPermissionForRemoveGroupMember, domName, groupName));
            return;
        }
        if (group.getAdmin() && notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotDominionOwnerForRemoveGroupMember, domName));
            return;
        }
        PlayerDTO player = PlayerDTO.select(playerName);
        if (player == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotExist, playerName));
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotMember, playerName, domName));
            return;
        }
        if (!Objects.equals(privilege.getGroupId(), group.getId())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotInGroup, playerName, groupName));
            return;
        }
        privilege = privilege.setGroupId(-1);
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DatabaseError));
            return;
        }
        operator.setResponse(SUCCESS);
    }

}
