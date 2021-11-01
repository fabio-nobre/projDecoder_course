package com.ead.course.validation;

import com.ead.course.clients.CourseClient;
import com.ead.course.dtos.CourseDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.UUID;

@Component
public class CourseValidator implements Validator {

    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;

    @Autowired
    CourseClient courseClient;

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CourseDto courseDto = (CourseDto) o;
        validator.validate(courseDto, errors);
        if(!errors.hasErrors()){
            validateUserInstructor(courseDto.getUserInstructor(), errors);
        }
    }

    private void validateUserInstructor(UUID userInstructor, Errors errors){
        UserDto userDto = courseClient.getOneUserById(userInstructor);
        if(userDto == null){
            errors.rejectValue("userInstructor", "UserInstructorError","Instructor not found.");
        } else if(userDto.getUserType().equals(UserType.STUDENT)){
            errors.rejectValue("userInstructor", "UserInstructorError","User must be INSTRUCTOR or ADMIN.");
        }
    }
}
