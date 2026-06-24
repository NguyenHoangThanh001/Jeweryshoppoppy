# ─────────────────────────────────────────────
# Stage 1: Build frontend assets
# ─────────────────────────────────────────────
FROM node:20-alpine AS builder
 
WORKDIR /app
 
# Copy package files first for caching
COPY package*.json ./
RUN npm ci
 
# Copy source and build
COPY . .
RUN npm run build
 
# ─────────────────────────────────────────────
# Stage 2: Serve with Nginx
# ─────────────────────────────────────────────
FROM nginx:1.27-alpine
 
# Remove default nginx page
RUN rm -rf /usr/share/nginx/html/*
 
# Copy built assets from the build stage
COPY --from=builder /app/dist /usr/share/nginx/html
 
# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf
 
EXPOSE 80
 
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://127.0.0.1:80 || exit 1
 
CMD ["nginx", "-g", "daemon off;"]