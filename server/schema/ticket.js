'use strict';

var Joi = require('joi');

module.exports = {
  date: Joi.date().format('YYYY-MM-DD'),
  arraySeatNumber: Joi.array().items(Joi.number().min(0)),
  cardNumber: Joi.number().min(1000000000000000).max(9999999999999999),
  cardSecurityCode: Joi.number().min(111).max(999),
  monthExpiration: Joi.number().min(1).max(12),
  yearExpiration: Joi.number().min(0).max(50)
};
