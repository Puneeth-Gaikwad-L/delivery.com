const express = require('express');
const InventoryRouter = express.Router();
const cli = require('cli-color');
const fetchcategories = require('../Service/InventoryService');

InventoryRouter.get('/category', async (req, res)=>{
    console.log(cli.redBright("Request Redirected to Inventory Controller! Forwarding to Service..."));
    return res.send(await fetchcategories());
})

module.exports = InventoryRouter;