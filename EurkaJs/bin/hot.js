#!/usr/bin/env node

/**
 * Module dependencies.
 */


var supervisor = require('supervisor');
/**
 * Supervisor Run www
 */

var args = new Array()
args[0] = 'bin/www';

supervisor.run(args);