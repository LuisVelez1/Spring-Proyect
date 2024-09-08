package Proyect.service.interfaces;

import Proyect.presentation.dto.ImageDTO;

public interface IImageService {
    ImageDTO uploadImageAndSaveURL(ImageDTO imageDTO);
}
