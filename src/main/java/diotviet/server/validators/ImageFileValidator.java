package diotviet.server.validators;

import diotviet.server.annotations.ValidImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    @Override
    public void initialize(ValidImage constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        // Output
        boolean result = true;

        try {
            String contentType = multipartFile.getContentType();
            // Check file content type
            if (Objects.isNull(contentType) || !isSupportedContentType(contentType)) {
                throw new Exception("Only PNG or JPG images are allowed.");
            }

            long fileSize = multipartFile.getSize();
            // Check file size
            switch ((int) fileSize) {
                case 5242880 -> throw new Exception("Maximum file size is 5MB.");
                case 0 -> throw new Exception("Empty file is not allowed.");
            }
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addConstraintViolation();

            result = false;
        }

        return result;
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Validate content type
     *
     * @param contentType
     * @return
     */
    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }
}
