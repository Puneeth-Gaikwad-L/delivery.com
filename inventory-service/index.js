const express = require('express');
const dotenv = require('dotenv');
const inventoryRoutes = require('./api/inventory');

dotenv.config();
const app = express();

// Middleware
app.use(express.json());

app.use('/api/products', inventoryRoutes);
