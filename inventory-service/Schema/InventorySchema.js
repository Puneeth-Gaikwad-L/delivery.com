const mongoose = require('mongoose')
const Schema = mongoose.Schema;

const categorySchema = new Schema({
  itemPath: {
    type: String,
    required: true,
    trim: true,
  },
  itemName: {
    type: String,
    required: true,
    trim: true,
    unique: true
  },
  itemImage: {
    darkImg: {
      type: String,
      required: true,
    },
    lightImg: {
      type: String,
      required: true,
    },
  },
}, { collection: "inventory" });

module.exports = mongoose.model('InventoryItem', categorySchema);