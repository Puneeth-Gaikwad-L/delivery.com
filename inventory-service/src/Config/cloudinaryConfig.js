const Logger = require('./logger');

const cloudinary = require('cloudinary').v2;

cloudinary.config({
  cloud_name: 'dhxuxqgdi',
  api_key: '677261143284791',
  api_secret: 'yg-P6GcbAQc3ez6PhCL0lzX1f74',
});


// Upload function with predefined settings
const uploadImage = async (fileBuffer, mimetype, options = {}) => {
    try {
        // Convert buffer to base64
        const fileStr = `data:${mimetype};base64,${fileBuffer.toString('base64')}`;
        
        // Default upload options
        const defaultOptions = {
            folder: 'categories',
            resource_type: 'auto',
            transformation: [
                { width: 500, height: 500, crop: 'fill' },
                { quality: 'auto' }
            ],
            ...options // Merge with custom options
        };
        
        const uploadResponse = await cloudinary.uploader.upload(fileStr, defaultOptions);
        return uploadResponse;
    } catch (error) {
        Logger.error(`Error while file upload: ${error.message}`)
        throw new Error(`Cloudinary upload failed: ${error.message}`);
    }
};

// Delete image function
const deleteImage = async (publicId) => {
    try {
        const result = await cloudinary.uploader.destroy(publicId);
        return result;
    } catch (error) {
        Logger.error(`Error while file deletion: ${error.message}`)
        throw new Error(`Cloudinary delete failed: ${error.message}`);
    }
};

module.exports = {
    cloudinary,
    uploadImage,
    deleteImage
};