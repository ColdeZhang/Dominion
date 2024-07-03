package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.*;

import java.util.Objects;

import static cn.lunadeer.dominion.controllers.Apis.noAuthToChangeFlags;
import static cn.lunadeer.dominion.controllers.Apis.notOwner;

public class GroupController {

    public static void createGroup(AbstractOperator operator, String domName, String groupName) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "创建权限组 %s 失败", groupName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "创建权限组 %s 成功", groupName);
        if (groupName.contains(" ")) {
            operator.setResponse(FAIL.addMessage("权限组名称不能包含空格"));
            return;
        }
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在", domName));
            return;
        }
        if (notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage("你不是领地 %s 的拥有者，无法创建权限组", domName));
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), groupName);
        if (group != null) {
            operator.setResponse(FAIL.addMessage("领地 %s 已存在名为 %s 的权限组", domName, groupName));
            return;
        }
        group = GroupDTO.create(groupName, dominion);
        if (group == null) {
            operator.setResponse(FAIL.addMessage("请联系服务器管理员"));
            return;
        }
        operator.setResponse(SUCCESS);
    }

    public static void deleteGroup(AbstractOperator operator, String domName, String groupName) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "删除权限组 %s 失败", groupName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "删除权限组 %s 成功", groupName);
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在", domName));
            return;
        }
        if (notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage("你不是领地 %s 的拥有者，无法删除权限组", domName));
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), groupName);
        if (group == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在名为 %s 的权限组", domName, groupName));
            return;
        }
        group.delete();
        operator.setResponse(SUCCESS);
    }

    public static void setGroupFlag(AbstractOperator operator, String domName, String groupName, String flag, boolean value) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "设置权限组 %s 的权限 %s 为 %s 失败", groupName, flag, value);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "设置权限组 %s 的权限 %s 为 %s 成功", groupName, flag, value);
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在", domName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) {
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), groupName);
        if (group == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在名为 %s 的权限组", domName, groupName));
            return;
        }
        if ((flag.equals("admin") || group.getAdmin()) && notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage("你不是领地 %s 的拥有者，无法修改管理员权限组权限", domName));
            return;
        }
        if (flag.equals("admin")) {
            group = group.setAdmin(value);
        } else {
            Flag f = Flag.getFlag(flag);
            if (f == null) {
                operator.setResponse(FAIL.addMessage("未知的权限 %s", flag));
                return;
            }
            group = group.setFlagValue(f, value);
        }
        if (group == null) {
            operator.setResponse(FAIL.addMessage("请联系服务器管理员"));
            return;
        }
        operator.setResponse(SUCCESS);
    }

    public static void renameGroup(AbstractOperator operator, String domName, String oldName, String newName) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "重命名权限组 %s 为 %s 失败", oldName, newName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "重命名权限组 %s 为 %s 成功", oldName, newName);
        if (newName.contains(" ")) {
            operator.setResponse(FAIL.addMessage("权限组名称不能包含空格"));
            return;
        }
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在", domName));
            return;
        }
        if (notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage("你不是领地 %s 的拥有者，无法重命名权限组", domName));
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), oldName);
        if (group == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在名为 %s 的权限组", domName, oldName));
            return;
        }
        group = group.setName(newName);
        if (group == null) {
            operator.setResponse(FAIL.addMessage("请联系服务器管理员"));
            return;
        }
        operator.setResponse(SUCCESS);
    }

    public static void addMember(AbstractOperator operator, String domName, String groupName, String playerName) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "添加成员 %s 到权限组 %s 失败", playerName, groupName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "添加成员 %s 到权限组 %s 成功", playerName, groupName);
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在", domName));
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), groupName);
        if (group == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在名为 %s 的权限组", domName, groupName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) {
            operator.setResponse(FAIL.addMessage("你没有权限修改领地 %s 的权限组 %s 成员", domName, groupName));
            return;
        }
        if (group.getAdmin() && notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage("你不是领地 %s 的拥有者，无法添加成员到管理员权限组", domName));
            return;
        }
        PlayerDTO player = PlayerDTO.select(playerName);
        if (player == null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不存在", playerName));
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不是领地 %s 的成员，无法直接加入权限组", playerName, domName));
            return;
        }
        if (Objects.equals(privilege.getGroupId(), group.getId())) {
            operator.setResponse(FAIL.addMessage("玩家 %s 已在权限组 %s 中", playerName, groupName));
            return;
        }
        if (notOwner(operator, dominion) && privilege.getAdmin()) {
            operator.setResponse(FAIL.addMessage("%s 是管理员，你不是领地 %s 的拥有者，无法添加管理员到权限组", playerName, domName));
            return;
        }
        privilege = privilege.setGroupId(group.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage("请联系服务器管理员"));
            return;
        }
        operator.setResponse(SUCCESS);
    }

    public static void removeMember(AbstractOperator operator, String domName, String groupName, String playerName) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "从权限组 %s 移除成员 %s 失败", groupName, playerName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "从权限组 %s 移除成员 %s 成功", groupName, playerName);
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在", domName));
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), groupName);
        if (group == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在名为 %s 的权限组", domName, groupName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) {
            operator.setResponse(FAIL.addMessage("你没有权限移除领地 %s 的权限组 %s 成员", domName, groupName));
            return;
        }
        if (group.getAdmin() && notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage("你不是领地 %s 的拥有者，无法从管理员权限组移除成员", domName));
            return;
        }
        PlayerDTO player = PlayerDTO.select(playerName);
        if (player == null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不存在", playerName));
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不是领地 %s 的成员", playerName, domName));
            return;
        }
        if (!Objects.equals(privilege.getGroupId(), group.getId())) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不在权限组 %s 中", playerName, groupName));
            return;
        }
        privilege = privilege.setGroupId(-1);
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage("请联系服务器管理员"));
            return;
        }
        operator.setResponse(SUCCESS);
    }

}
