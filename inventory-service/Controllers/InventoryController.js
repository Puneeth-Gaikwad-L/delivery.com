const express = require("express");
const InventoryRouter = express.Router();
const cli = require("cli-color");
const multer = require("multer");
const { uploadImage } = require("../Config/cloudinaryConfig"); // Import cloudinary config
const { fetchCategories } = require("../Service/InventoryService");
const { updateCategory } = require("../Service/InventoryService"); // Assuming you have this service

// Configure multer for file uploads
const storage = multer.memoryStorage();
const upload = multer({
    storage: storage,
    limits: {
        fileSize: 5 * 1024 * 1024, // 5MB limit
    },
    fileFilter: (req, file, cb) => {
        if (file.mimetype.startsWith("image/")) {
            cb(null, true);
        } else {
            cb(new Error("Only image files are allowed!"), false);
        }
    },
});

InventoryRouter.get("/category", async (req, res) => {
    console.log(
        cli.redBright(
            "Request Redirected to Inventory Controller! Forwarding to Service..."
        )
    );
    try {
        const categories = await fetchCategories();
        return res.status(200).json({
            success: true,
            data: categories,
        });
    } catch (error) {
        console.log(cli.red("Error fetching categories:", error.message));
        return res.status(500).json({
            success: false,
            message: "Failed to fetch categories",
            error: error.message,
        });
    }
});

InventoryRouter.put("/edit", upload.single("photo"), async (req, res) => {
    console.log(
        cli.yellowBright(
            "Request Redirected to Inventory Controller for category update!"
        )
    );

    try {
        const { id, name } = req.body;

        // Validate required fields
        if (!id) {
            return res.status(400).json({
                success: false,
                message: "Category ID is required",
            });
        }

        if (!name) {
            return res.status(400).json({
                success: false,
                message: "Category name is required",
            });
        }

        let photoUrl = null;

        // Handle photo upload if provided
        if (req.file) {
            console.log(cli.blue("Uploading image to Cloudinary..."));

            // Upload to Cloudinary using the config function
            const uploadResponse = await uploadImage(
                req.file.buffer,
                req.file.mimetype
            );

            photoUrl = uploadResponse.secure_url;
            console.log(cli.green("Image uploaded successfully to Cloudinary"));
        }

        // Prepare update data
        const updateData = {
            $set: {
                ...(name && { itemName: name }),
                ...(photoUrl && {
                    "itemImage.lightImg": photoUrl,
                    "itemImage.darkImg": photoUrl,
                }),
            },
        };

        // Update category in database
        const updatedCategory = await updateCategory(id, updateData);

        console.log(cli.green("Category updated successfully"));

        return res.status(200).json({
            success: true,
            message: "Category updated successfully",
            data: updatedCategory,
        });
    } catch (error) {
        console.log(cli.red("Error updating category:", error.message));

        // Handle specific Cloudinary errors
        if (error.name === "CloudinaryError") {
            return res.status(400).json({
                success: false,
                message: "Failed to upload image to Cloudinary",
                error: error.message,
            });
        }

        // Handle multer errors
        if (error.code === "LIMIT_FILE_SIZE") {
            return res.status(400).json({
                success: false,
                message: "File size too large. Maximum size is 5MB",
            });
        }

        return res.status(500).json({
            success: false,
            message: "Failed to update category",
            error: error.message,
        });
    }
});

module.exports = InventoryRouter;
