package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.dto.UserInfoUpdateDTO;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.exception.ValidationErrorException;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.model.UserInfo;
import com.caiohbs.crowdcontrol.repository.UserInfoRespository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserInfoService {

    private final UserRepository userRepository;
    private final UserInfoRespository userInfoRespository;

    public UserInfoService(
            UserRepository userRepository,
            UserInfoRespository userInfoRespository
    ) {
        this.userRepository = userRepository;
        this.userInfoRespository = userInfoRespository;
    }

    /**
     * Creates and persists additional user information for a given user ID.
     * This method attempts to create a new UserInfo object associated with the
     * provided userId.
     *
     * @param userId   The ID of the user for whom to create user information.
     * @param userInfo The user information object containing details like profile
     *                 picture filename, pronouns, bio, and nationality.
     * @return The newly created and persisted UserInfo object.
     * @throws ResourceNotFoundException If a user with the provided userId is
     *                                   not found.
     * @throws ValidationErrorException  If the user already has existing user
     *                                   information.
     */
    public UserInfo createInfo(
            Long userId, UserInfo userInfo
    ) throws ResourceNotFoundException, ValidationErrorException {

        Optional<User> foundUser = userRepository.findById(userId);

        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        if (foundUser.get().getUserInfo() != null) {
            throw new ValidationErrorException(
                    "User info already initialized. " +
                    "Please use the update endpoint to make changes to it."
            );
        }

        UserInfo newUserInfo = new UserInfo(
                foundUser.get(), convertFileName(userInfo.getPfp()),
                userInfo.getPronouns(), userInfo.getBio(), userInfo.getNationality()
        );

        userInfoRespository.save(newUserInfo);

        return newUserInfo;

    }

    /**
     * Retrieves user information for a given user ID.
     * This method fetches the UserInfo associated with the specified userId.
     *
     * @param userId The ID of the user whose information to retrieve.
     * @return The UserInfo object for the specified user.
     * @throws ResourceNotFoundException If no user is found with the provided
     *                                   userId.
     */
    public UserInfo retrieveInfo(Long userId) {

        Optional<User> foundUser = userRepository.findById(userId);

        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        return foundUser.get().getUserInfo();

    }

    /**
     * Updates user information for a given user ID.
     * This method allows updating specific fields of the user information a
     * ssociated with the provided userId.
     *
     * @param userId        The ID of the user whose information to update.
     * @param updateInfoDTO The DTO object containing fields to be updated.
     * @return A message indicating success ("Success") or the converted image
     * filename (if profile picture was updated).
     * @throws ResourceNotFoundException If no user is found with the provided
     *                                   userId.
     * @throws ValidationErrorException  If the user has no existing user
     *                                   information or an exception occurs
     *                                   during pronoun update.
     */
    public String updateInfo(
            Long userId, UserInfoUpdateDTO updateInfoDTO
    ) throws ResourceNotFoundException, ValidationErrorException {

        Optional<User> user = userRepository.findById(userId);
        String result = "Success";

        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        UserInfo foundUser = user.get().getUserInfo();

        if (foundUser == null) {
            throw new ValidationErrorException("User info not initialized.");
        }

        if (updateInfoDTO.isPfpPresent()) {
            result = convertFileName(updateInfoDTO.pfp());
            foundUser.setPfp(result);
        }
        if (updateInfoDTO.isPronounsPresent()) {
            try {
                foundUser.setPronouns(updateInfoDTO.pronouns());
            } catch (IllegalArgumentException e) {
                throw new ValidationErrorException(e.getMessage());
            }
        }
        if (updateInfoDTO.isBioPresent()) {
            foundUser.setBio(updateInfoDTO.bio());
        }
        if (updateInfoDTO.isNationalityPresent()) {
            foundUser.setNationality(updateInfoDTO.nationality());
        }

        userInfoRespository.save(foundUser);
        return result;

    }

    /**
     * This method receives the original file name WITH THE EXTENSION INCLUDED
     * and returns the new name with randomized characters to prevent
     * name-clashing when saving files.
     * NOTE: the files WILL NOT be stored by Spring, the front-end will simply
     * receive the new name for the file that is to be stored and served by
     * another app in the stack. This whole method just facilitates saving the
     * file path to database.
     *
     * @param originalFileName The original name of the file, including the
     *                         original extension.
     * @return The result of the generation, including the extension of
     * the file at the end.
     */
    public String convertFileName(String originalFileName) {

        String newName = UUID.randomUUID() + "_" + originalFileName;

        if (newName.length() >= 255) {
            newName = newName.substring(newName.length() - 255);
        }

        return newName;

    }

}
