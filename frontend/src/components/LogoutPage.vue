<template>
  <div class="logout-container">
    <div class="logout-card">
      <h3>{{ message }}</h3>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()
const message = ref("Logging you out...")

onMounted(() => {
  // Clear stored login session if needed
  localStorage.removeItem("token")  
  sessionStorage.clear()             //clear session

  setTimeout(() => {
    message.value = "You have been logged out! Thank you for visiting the website!"
  }, 1200)

  setTimeout(() => {
    router.push("/login")
  }, 3000)
})
</script>

<style scoped>
.logout-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(to bottom right, #3b82f6, #1e3a8a);
  display: flex;
  justify-content: center;
  align-items: center;
}

.logout-card {
  background: #ffffff;
  padding: 24px 40px;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #08306b;
  box-shadow: 0 6px 18px rgba(0,0,0,0.18);
  text-align: center;
  animation: fadeIn 0.5s ease-out;
}

/* Smooth fade */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}
</style>
