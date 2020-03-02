import { BlobServiceClient, StorageSharedKeyCredential } from '@azure/storage-blob';
import { Injectable, OnApplicationBootstrap } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { NoteImage } from './models/note-image-schema';

@Injectable()
export class AzureBlobStorageService implements OnApplicationBootstrap {
    private readonly mBlobStorageClient: BlobServiceClient;
    private readonly mContainerId: string;

    constructor(private readonly configService: ConfigService) {
        const account: string = this.configService.get<string>('AZURE-BLOB-STORAGE-ACCOUNT') || '<blob storage account>';
        const accountKey: string = this.configService.get<string>('AZURE-BLOB-STORAGE-ACCESS-KEY') || '<access key>';
        const storageURL: string = this.configService.get<string>('AZURE-BLOB-STORAGE-URL') || '<blob storage url>';
        this.mContainerId = this.configService.get<string>('AZURE-BLOB-CONTAINER-ID') || '<blob container id>';
        
        const sharedKeyCredential: StorageSharedKeyCredential = new StorageSharedKeyCredential(account, accountKey);
        this.mBlobStorageClient = new BlobServiceClient(
            storageURL,
            sharedKeyCredential,
          );
    }

    async onApplicationBootstrap() {
        try {
            const containerClient = this.mBlobStorageClient.getContainerClient(this.mContainerId);
            const createContainerResponse = await containerClient.create();
            // tslint:disable-next-line
            console.log(`Create container ${this.mContainerId} successfully`, createContainerResponse.requestId);
        } catch (error) {
            // tslint:disable-next-line
            console.log(`Error creating container ${this.mContainerId}:%s\n`, error);
        }

        return;
    }

    async AddImage(noteImage: NoteImage) {
        try {
            const containerClient = this.mBlobStorageClient.getContainerClient(this.mContainerId);
 
            const blockBlobClient = containerClient.getBlockBlobClient(noteImage.NoteId);
            const uploadBlobResponse = await blockBlobClient.upload(noteImage.Url, noteImage.Url.length);
            // tslint:disable-next-line
            console.log(`Uploaded image url for note ${noteImage.NoteId} successfully`, uploadBlobResponse.requestId);

        } catch (error) {
            // tslint:disable-next-line
            console.log(`Error adding image:%s\n`, error);
        }

        return;
    }

    async GetImage(noteId: string) {
        try {
            const containerClient = this.mBlobStorageClient.getContainerClient(this.mContainerId);
            const blockBlobClient = containerClient.getBlockBlobClient(noteId);
  
            // Get blob content from position 0 to the end
            const downloadBlockBlobResponse = await blockBlobClient.download();
            const downloaded = await this.streamToString(downloadBlockBlobResponse.readableStreamBody);
            // tslint:disable-next-line
            console.log("Downloaded blob content:", downloaded);

            return downloaded;
        } catch (error) {
            // tslint:disable-next-line
            console.log(`Error getting image:%s\n`, error);
        }

        return;
    }

    // helper method used to read a readable stream into string
    // https://www.npmjs.com/package/@azure/storage-blob
    async streamToString(readableStream) {
        return new Promise((resolve, reject) => {
        const chunks = [];
        readableStream.on('data', (data) => {
            chunks.push(data.toString());
        });
        readableStream.on('end', () => {
            resolve(chunks.join(''));
        });
        readableStream.on('error', reject);
        });
    }
}
