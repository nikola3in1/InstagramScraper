
const express = require('express');
const app = express();
let fs = require('fs');
let bodyParser = require("body-parser");
app.use(bodyParser.json());

app.get('/cookie',(req,res)=>{
    let user = req.query.username;
    let pass = req.query.password;

    console.log("Grabbing cookies of "+user+"...");
    const puppeteer = require('puppeteer');
    puppeteer.launch({ headless: true }).then(async browser => {
        const page = await browser.newPage();
        await page.setViewport({ width: 640, height: 400 })
        await page.goto('https://www.instagram.com/accounts/login/?force_classic_login', { waitUntil: 'networkidle2' });

        await page.type('#id_username', user);
        await page.type('#id_password', pass);
        await page.click(".button-green");

        await page.waitForNavigation();
        const cookies = await page.cookies();
        await browser.close();

        let data = {
            "cookie": cookies[2],
            "account": user
        };
        res.send(data);
    });
});

app.listen(4000, () => {
    console.log("Listening on port 4000")
});
