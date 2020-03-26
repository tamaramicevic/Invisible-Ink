import {
AzureStorageFileInterceptor,
AzureStorageService,
UploadedFileMetadata,
} from '@nestjs/azure-storage';
import { Controller,
    Logger,
    Param,
    Post,
    UploadedFile,
    UseInterceptors,  
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { AzureCosmosDbService } from 'src/azure-db/azure-cosmos-db.service';
import { UploadImageResponse } from './models/upload-image-response';

@Controller('photo')
export class UploadImageController {
    constructor(
        private readonly azureStorageService: AzureStorageService,
        private readonly azureCosmosDbService: AzureCosmosDbService) {}
    @Post(':noteId')
    @UseInterceptors(
        AzureStorageFileInterceptor('file'),
    )
    async UploadedFilesUsingInterceptor(
        @Param('noteId') noteId: string, 
        @UploadedFile() file: UploadedFileMetadata): Promise<UploadImageResponse> {
        Logger.log(`Uploading image for id: ${noteId}`, 'UploadImageController');
        file = {
        ...file,
        originalname: noteId,
        };
        const storageUrl = await this.azureStorageService.upload(file);
        if (storageUrl !== null) {
            Logger.log(`Successfully uploaded image with ID: ${noteId}`, 'UploadImageController');
            await this.azureCosmosDbService.AssignImageToNote(noteId, storageUrl);
            return { success: true };
        } else {
            return { success: false, error: 'Image upload failed.' }; 
        }
    }
}
