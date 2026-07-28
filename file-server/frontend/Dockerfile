# ===== 构建阶段 =====
FROM node:22-alpine AS builder
WORKDIR /build
COPY package.json package-lock.json ./
RUN npm ci --silent
COPY . .
RUN npx ng build --configuration production

# ===== 运行阶段 =====
FROM nginx:alpine
COPY --from=builder /build/dist/frontend/browser /usr/share/nginx/html
COPY deploy/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
