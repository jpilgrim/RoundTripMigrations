const puppeteer = require('puppeteer');

const express = require('express');
const app = express();

const mkdirp = require('mkdirp');

const outputDir = "graphs/";

const port = 3111;

(async () => {
    app.get('/graph/current/pdf', async (req, res) => {
        const browser = await puppeteer.launch();
        const page = await browser.newPage();

        await page.goto('http://localhost:2121/graph/current', {waitUntil: 'networkidle2'});

        let rect = await page.evaluate(() => {
            return {
                width: document.documentElement.offsetWidth,
                height: document.documentElement.offsetHeight
        }});

        let moduleName = await page.evaluate(() => {
            return moduleName;
        })

        let segments = moduleName.split("/");
        let filename = segments.pop();

        let directory = outputDir + segments.join("/");

        mkdirp(directory, async (err) => {
            let pdfPath = directory + "/" + filename + ".pdf";

            let pdf = await page.pdf({
                path: pdfPath,
                width: rect.width + "px", 
                height: rect.height + "px"});

            console.log(`Saved Object Graph for "${moduleName}" at ${pdfPath}`)

            res.setHeader("Access-Control-Allow-Origin", "http://localhost:2121");
            res.send(process.cwd() + "/" + pdfPath);

            await browser.close();
        })
    })

    await app.listen(port, "localhost", () => console.log(`The current ObjectGraph can now be retrieved as PDF from http://localhost:${port}/graph/current/pdf`));
})();