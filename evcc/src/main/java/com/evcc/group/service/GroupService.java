package com.evcc.group.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.group.entity.Group;
import com.evcc.group.entity.GroupRole;
import com.evcc.group.entity.Membership;
import com.evcc.group.entity.MembershipStatus;
import com.evcc.group.repository.GroupRepository;
import com.evcc.group.repository.MembershipRepository;




@Service
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;

    public GroupService(GroupRepository groupRepository, MembershipRepository membershipRepository) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
    }

    /**
     * Get all groups
     */
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    /**
     * Find group by ID
     */
    public Optional<Group> findGroupById(UUID id) {
        return groupRepository.findById(id);
    }

    /**
     * Find group by name
     */
    public Optional<Group> findGroupByName(String name) {
        return groupRepository.findByName(name);
    }

    /**
     * Create a new group
     */
    public Group createGroup(String name, String description, UUID contractId) {
        if (groupRepository.existsByName(name)) {
            throw new RuntimeException("Group name already exists: " + name);
        }

        Group group = new Group(name, description, contractId);
        return groupRepository.save(group);
    }

    /**
     * Create a new group and add owner
     */
    public Group createGroupWithOwner(String name, String description, UUID contractId, UUID ownerId) {
        Group group = createGroup(name, description, contractId);
        
        // Add owner membership
        Membership membership = new Membership(group.getId(), ownerId, GroupRole.CO_OWNER);
        membershipRepository.save(membership);
        
        return group;
    }

    /**
     * Update group
     */
    public Group updateGroup(UUID id, String name, String description, UUID contractId) {
        Optional<Group> groupOpt = groupRepository.findById(id);
        if (groupOpt.isEmpty()) {
            throw new RuntimeException("Group not found with id: " + id);
        }

        Group group = groupOpt.get();

        // Check if name is taken by another group
        if (!group.getName().equals(name) && groupRepository.existsByName(name)) {
            throw new RuntimeException("Group name already exists: " + name);
        }

        group.setName(name);
        group.setDescription(description);
        group.setContractId(contractId);

        return groupRepository.save(group);
    }

    /**
     * Delete group
     */
    public boolean deleteGroup(UUID id) {
        if (groupRepository.existsById(id)) {
            // Delete all memberships first
            List<Membership> memberships = membershipRepository.findByGroupId(id);
            membershipRepository.deleteAll(memberships);
            
            // Delete group
            groupRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get groups by contract ID
     */
    public List<Group> findGroupsByContractId(UUID contractId) {
        return groupRepository.findByContractId(contractId);
    }

    /**
     * Search groups by name
     */
    public List<Group> searchGroupsByName(String name) {
        return groupRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Get groups without contract
     */
    public List<Group> getGroupsWithoutContract() {
        return groupRepository.findByContractIdIsNull();
    }

    /**
     * Get groups with active members
     */
    public List<Group> getGroupsWithActiveMembers() {
        return groupRepository.findGroupsWithActiveMembers();
    }

    /**
     * Get total group count
     */
    public long getTotalGroups() {
        return groupRepository.count();
    }

    /**
     * Get group member count
     */
    public long getGroupMemberCount(UUID groupId) {
        return membershipRepository.countByGroupIdAndStatus(groupId, MembershipStatus.ACTIVE);
    }

    /**
     * Check if group exists
     */
    public boolean groupExists(UUID id) {
        return groupRepository.existsById(id);
    }

    /**
     * Check if group name exists
     */
    public boolean groupNameExists(String name) {
        return groupRepository.existsByName(name);
    }
}
