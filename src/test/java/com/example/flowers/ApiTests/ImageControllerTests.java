package com.example.flowers.ApiTests;
import com.example.flowers.controllers.ImageController;
import com.example.flowers.models.Image;
import com.example.flowers.repositories.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageControllerTests {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageController imageController;

    private Image testImage;

    @BeforeEach
    void setUp() {
        testImage = new Image();
        testImage.setId(1L);
        testImage.setName("test.jpg");
        testImage.setOriginalFileName("original_test.jpg");
        testImage.setSize(1024L);
        testImage.setContentType("image/jpeg");
        testImage.setPreviewImage(true);
        testImage.setBytes(new byte[]{1, 2, 3, 4});
    }

    @Test
    void getImageById_WhenImageExists_ReturnsImageWithCorrectHeaders() {
        // Arrange
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // Act
        ResponseEntity<?> response = imageController.getImageById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("original_test.jpg", response.getHeaders().getFirst("fileName"));
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertEquals(1024L, response.getHeaders().getContentLength());
        assertTrue(response.getBody() instanceof InputStreamResource);

        verify(imageRepository, times(1)).findById(1L);
    }

    @Test
    void getImageById_WhenContentTypeIsNull_ThrowsInvalidMediaTypeException() {
        // Arrange
        testImage.setContentType(null);
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // Act & Assert
        assertThrows(InvalidMediaTypeException.class, () -> {
            imageController.getImageById(1L);
        });

        verify(imageRepository, times(1)).findById(1L);
    }

    @Test
    void getImageById_WhenBytesAreNull_ThrowsNullPointerException() {
        // Arrange
        testImage.setBytes(null);
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            imageController.getImageById(1L);
        });

        verify(imageRepository, times(1)).findById(1L);
    }

    @Test
    void getImageById_WhenDifferentContentType_ReturnsCorrectMediaType() {
        // Arrange
        testImage.setContentType("image/png");
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // Act
        ResponseEntity<?> response = imageController.getImageById(1L);

        // Assert
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
    }

    @Test
    void getImageById_WhenEmptyBytes_ReturnsEmptyResource() throws IOException {
        // Arrange
        testImage.setBytes(new byte[0]);
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // Act
        ResponseEntity<?> response = imageController.getImageById(1L);

        // Assert
        assertNotNull(response.getBody());
        InputStreamResource resource = (InputStreamResource) response.getBody();
        assertEquals(0, resource.contentLength());
    }
}