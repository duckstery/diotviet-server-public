package diotviet.server.services;

import com.fasterxml.jackson.databind.JsonNode;
import diotviet.server.entities.Image;
import diotviet.server.repositories.ImageRepository;
import diotviet.server.utils.OtherUtils;
import diotviet.server.utils.StorageUtils;
import diotviet.server.validators.ImageValidator;
import diotviet.server.views.Identifiable;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ImageService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Image repository
     */
    @Autowired
    private ImageRepository repository;
    /**
     * Image validator
     */
    @Autowired
    private ImageValidator validator;
    /**
     * Storage utils
     */
    @Autowired
    private StorageUtils storageUtils;

    // ****************************
    // Public API
    // ****************************

    /**
     * Pull Image list for Identifiable
     *
     * @param type
     * @param identifiableId
     * @return
     */
    public List<Image> pull(String type, Long identifiableId) {
        return repository.findAllByIdentifiableIdAndType(type, identifiableId);
    }

    /**
     * Upload image
     *
     * @param files
     * @return
     */
    public void uploadAndSave(Identifiable entity, List<MultipartFile> files) {
        // Create output
        List<Image> images = new ArrayList<>();

        try {
            // Iterate through each MultipartFile
            for (MultipartFile file : files) {
                // Upload each MultipartFile to ImgBB
                JsonNode json = storageUtils.upload(file);
                // Convert json to Image and add to images list
                images.add(findOrCreateImage(entity, json));
            }
        } catch (IOException e) {
            validator.interrupt("upload_fail", "", "file");
        }

        // First save all Images since no Cascade is set
        repository.saveAll(images);
    }

    /**
     * Delete connection between Identifiable and Image
     * @param type
     * @param ids
     */
    public void delete(String type, Long[] ids) {
        // Delete link between Identifiable and Image
        Set<Long> imageIds = repository.deleteImageAssocByTypeAndIdentifiableId(type, ids);

        // Only need to check for unused Image if any assoc is deleted
        if (!imageIds.isEmpty()) {
            // Count occurrence of Image to make sure, Image is not used by other Identifiable
            List<Long> inUseImageIds = repository.findInUseImageIdByImgIds(imageIds.toArray(Long[]::new));
            // Remove in use Image out of imageIds
            inUseImageIds.forEach(imageIds::remove);
        }

        // Get uid of these unused Image
        if (!imageIds.isEmpty()) {
            // Delete Images in database, then return it's ImgBB uid
            List<String> imageUIds = repository.deleteByIdsReturningUid(imageIds.toArray(Long[]::new));
            // Delete these Images on ImgBB
            storageUtils.delete(imageUIds);
        }
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Convert JsonNode to Image
     *
     * @param json
     * @return
     */
    private Image findOrCreateImage(Identifiable entity, JsonNode json) {
        // Get root node
        JsonNode root = json.get("data");
        // Get uid
        String uid = root.get("id").asText("");

        // Try to find Image in db, if Image is not exists, create a new one
        Image image = repository.findByUid(uid).orElseGet(() -> (new Image())
                .setUid(uid)
                .setSrc(root.get("url").asText(""))
                .setIsActive(false)
                .setOwners(new ArrayList<>()));
        // Add new owner to Image
        image.getOwners().add(entity);
        System.out.println(image.getOwners());
        return image;
    }
}
