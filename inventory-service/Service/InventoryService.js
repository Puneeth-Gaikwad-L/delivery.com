const cli = require("cli-color");
const InventorySchema = require("../Schema/InventorySchema");

const fetchCategories = async () => {
  console.log(cli.blueBright("Request received at Service!"));
  try {
    const item = await InventorySchema.find();
    console.log(cli.bgGreenBright("categories list fetched successfully"));
    return item;
  } catch (err) {
    console.log(
      cli.yellowBright("Error occured while fetching categories Data: " + err)
    );
  }
};

// In your InventoryService file
const updateCategory = async (id, updateData) => {
  console.log(cli.blueBright("Update request received:"), updateData);
  try {
    if (!id) {
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

    console.log(cli.bgGreenBright("Category updated successfully"));
    return updatedCategory;
  } catch (err) {
    console.log(
      cli.yellowBright("Error occurred while updating category: " + err)
    );
    throw err;
  }
};

module.exports = { fetchCategories, updateCategory };
