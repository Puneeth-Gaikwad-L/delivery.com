const cli = require("cli-color");
const InventorySchema = require("../Schema/InventorySchema");
const Logger = require("../Config/logger");

const fetchCategories = async () => {
  Logger.info("Request received at Service for fetching categories!");
  try {
    const item = await InventorySchema.find();
    Logger.info("categories list fetched successfully");
    return item;
  } catch (err) {
    Logger.error(`Error occured while fetching categories Data: ${err}`);
  }
};

// In your InventoryService file
const updateCategory = async (id, updateData) => {
  Logger.info(`Update request received: ${updateData}`);
  try {
    if (!id) {
      Logger.error("Category ID is required for update")
      throw new Error("Category ID is required for update");
    }

    const updatedCategory = await InventorySchema.findByIdAndUpdate(
      id,
      updateData,
      { new: true, runValidators: true }
    );

    if (!updatedCategory) {
      throw new Error("Category not found");
    }

    Logger.info("Category updated successfully");
    return updatedCategory;
  } catch (err) {
    Logger.error(`Error occurred while updating category: ${err}`);
    throw err;
  }
};

module.exports = { fetchCategories, updateCategory };
