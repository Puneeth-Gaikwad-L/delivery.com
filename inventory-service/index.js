const express = require('express')
const cli = require('cli-color')
require('./Config/eurekaClient');
require("dotenv").config()

const db = require('./Config/db');
const InventoryRouter = require('./Controllers/InventoryController');
const app = express();
const PORT = process.env.PORT || 8001;

app.get('/', (req, res) => {
    return res.send({
        statue: 200,
        message: "inventory server is up!"
    })
})

app.use('/api/inventory', InventoryRouter)

app.listen(PORT, ()=>{
    console.log(cli.yellowBright(`Inventory server is running on port ${PORT}`));
})