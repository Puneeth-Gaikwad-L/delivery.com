// config/multer.js
const multer = require('multer');

// Configure multer for file uploads
const storage = multer.memoryStorage();

const fileFilter = (req, file, cb) => {
    if (file.mimetype.startsWith('image/')) {
        cb(null, true);
    } else {
        cb(new Error('Only image files are allowed!'), false);
    }
};

const upload = multer({
    storage: storage,
    limits: {
        fileSize: 5 * 1024 * 1024 // 5MB limit
    },
    fileFilter: fileFilter
});

// Export different upload configurations
module.exports = {
    single: (fieldName) => upload.single(fieldName),
    multiple: (fieldName, maxCount) => upload.array(fieldName, maxCount),
    fields: (fields) => upload.fields(fields),
    any: () => upload.any()
};