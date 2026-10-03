# QCollect Cloudinary Photo Storage

## Security first

The Cloudinary API secret that was shared in chat must be rotated before deployment.
Never commit `CLOUDINARY_URL` to GitHub or place it in `application.properties`.

## Railway variables

Add these variables to the backend Railway service:

```text
CLOUDINARY_URL=cloudinary://<new_api_key>:<new_api_secret>@<cloud_name>
CLOUDINARY_FOLDER=qcollect
CLOUDINARY_MAX_IMAGE_SIZE_BYTES=15728640
```

`CLOUDINARY_MAX_IMAGE_SIZE_BYTES=15728640` means 15 MB per image.

## Existing API flow remains compatible

### Online multipart upload

```http
POST /submissions/{submissionId}/photos?latitude={latitude}&longitude={longitude}
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

Multipart field:

```text
file=<image>
```

The backend uploads the image directly to Cloudinary and stores:

- Cloudinary `public_id` in `submission_photos.file_path`;
- Cloudinary HTTPS URL in `submission_photos.photo_url`;
- `UPLOADED` in `submission_photos.upload_status`.

### QC photo retrieval

```http
GET /submissions/{submissionId}/photos
```

Each result keeps the existing response contract:

```json
{
  "id": "PHOTO_UUID",
  "fileName": "photo.jpg",
  "fileUrl": "/photos/PHOTO_UUID",
  "latitude": 28.6139,
  "longitude": 77.2090
}
```

```http
GET /photos/{photoId}
```

For Cloudinary-backed images, this endpoint redirects to the Cloudinary HTTPS delivery URL. Existing QC frontend code can therefore continue using `fileUrl`.

### Delete

```http
DELETE /photos/{photoId}
```

The Cloudinary asset is destroyed and invalidated, then the database row is soft-deleted.

## Offline sync

Both sync models now support base64 image data.

### `/sync/upload`

```json
{
  "deviceId": "DEVICE-1",
  "photos": [
    {
      "photoId": "PHOTO_UUID",
      "submissionId": "SUBMISSION_UUID",
      "image": "data:image/jpeg;base64,/9j/4AAQ...",
      "latitude": 28.6139,
      "longitude": 77.2090,
      "syncVersion": 0
    }
  ]
}
```

### `/mobile/sync`

```json
{
  "submissionId": "SUBMISSION_UUID",
  "syncVersion": 0,
  "photos": [
    {
      "fileName": "photo.jpg",
      "image": "data:image/jpeg;base64,/9j/4AAQ...",
      "latitude": 28.6139,
      "longitude": 77.2090,
      "deleted": false
    }
  ]
}
```

A phone-local path such as `/data/user/.../photo.jpg` cannot be read by the backend. For offline sync, the Flutter app must send base64 bytes in `image`, or use the multipart upload endpoint when connectivity returns.

## Flutter multipart example

```dart
final formData = FormData.fromMap({
  'file': await MultipartFile.fromFile(
    localPath,
    filename: localPath.split('/').last,
  ),
});

await dio.post(
  '/submissions/$submissionId/photos',
  queryParameters: {
    'latitude': latitude,
    'longitude': longitude,
  },
  data: formData,
);
```

## Legacy local photos

`PhotoUploadProcessor` now attempts to migrate existing `PENDING` local images to Cloudinary every 30 seconds. After a successful upload it stores the Cloudinary identifiers and removes the legacy local file.

## Files changed

- `pom.xml`
- `src/main/resources/application.properties`
- `src/main/java/com/example/qcollect/integration/cloudinary/*`
- `src/main/java/com/example/qcollect/submission/controller/PhotoController.java`
- `src/main/java/com/example/qcollect/submission/entity/SubmissionPhoto.java`
- `src/main/java/com/example/qcollect/submission/repository/SubmissionPhotoRepository.java`
- `src/main/java/com/example/qcollect/submission/service/SubmissionServiceImpl.java`
- `src/main/java/com/example/qcollect/submission/service/PhotoUploadProcessor.java`
- `src/main/java/com/example/qcollect/sync/service/SyncServiceImpl.java`
- `src/main/java/com/example/qcollect/mobile/dto/SyncPhotoRequest.java`
- `src/main/java/com/example/qcollect/mobile/service/MobileServiceImpl.java`
