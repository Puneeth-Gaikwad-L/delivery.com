const express = require("express");
const cli = require("cli-color");
const InventorySchema = require("../Schema/InventorySchema");

const fetchcategories = async () => {
  console.log(cli.redBright("Request received at Service!"));
  try{
    const item = await InventorySchema.find();
    console.log(cli.bgGreenBright("categories list fetched successfully"));
    return item;
  }catch(err){
    console.log(cli.yellowBright("Error occured while fetching categories Data: " + err));
  }
};

module.exports = fetchcategories;
