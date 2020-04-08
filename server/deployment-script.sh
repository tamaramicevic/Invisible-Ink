# Install npm and node
apt-get update
apt-get install nodejs
apt-get install npm

# Install dependencies
npm install

# Install pm2
pm2 start npm --no-automation --name InvisibleInk -- run start