const express = require('express');
const app = express();
const port = 3232;

const rpcData = {
    applicationId: "1286076438793027676",
    autoRegister: true,
    steamId: "",
    smallImageKey: "logo",
    details: "[ Aquatic High School ]",
    state: "By. DimensionStudio",
    useUsername: true
};

app.get('/rpc/init', (req, res) => {
    res.json(rpcData);
});

app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
