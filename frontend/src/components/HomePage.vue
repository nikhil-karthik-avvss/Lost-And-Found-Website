<template>
  <div class="home-container">
    <nav class="navbar">
      <div class="nav-logo">Lost & Found</div>

      <ul class="nav-links">
        <li @click="goTo('lost')">Lost Items</li>
        <li @click="goTo('found')">Found Items</li>
        <li @click="goTo('chat')">Chat</li>
        <li class="logout" @click="logout">Logout</li>
      </ul>
    </nav>

    <div class="content">
      <h2>Welcome to Lost & Found Portal 👋</h2>
      <p>Select an option from the navigation bar.</p>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router"
const router = useRouter()

function goTo(page) {
  router.push(`/${page}`)
}

function logout() {
  router.push("/logout")
}
</script>

<style scoped>

:global(html, body) {
  overflow-x: hidden;
}


:root {
  --primary: #0d47a1;
  --primary-hover: #08306b;
  --light-bg: linear-gradient(to bottom, #eaf1ff, #c3d7ff);
}

/* Full Page Container */
.home-container {
  width: 100%;
  min-height: 100vh;
  background: var(--light-bg);
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
}


/* Navigation Bar */
.navbar {
  width: 100%;
  min-height: 70px;
  background: var(--primary);
  display: flex;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 5px 18px rgba(0,0,0,0.25);

  box-sizing: border-box; /* ✅ Prevent width overshoot */
  overflow-x: hidden;     /* ✅ No horizontal scroll */
}



.nav-logo {
  color: white;
  font-size: 22px;
  font-weight: 700;
  margin-right: auto; /* ✅ This is the key fix */
}

/* Navigation Links */
.nav-links {
  display: flex;
  list-style: none;
  gap: 24px;
  align-items: center;
  flex-wrap: wrap; /* ✅ Prevent pushing items off-screen */
  max-width: 100%;
}



.nav-links li {
  color: #e9f0ff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: .25s;
  padding-bottom: 4px;
}

.nav-links li:hover,
.nav-links .active {
  color: #fff;
  border-bottom: 3px solid #fff;
}

/* Logout Style */
.logout {
  color: #ffd4d4 !important;
}
.logout:hover {
  color: #ffb8b8 !important;
  border-bottom: 3px solid #ffb8b8 !important;
}

/* Center Page Content */
.content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.content h2 {
  font-size: 30px;
  color: var(--primary-hover);
  margin-bottom: 10px;
}

.content p {
  font-size: 17px;
  color: #444;
}
</style>
