<template>
  <div class="auth-container">
    <div class="auth-card">

      <h2 class="auth-title">Sign In</h2>

      <!-- Username Field -->
      <label class="field-label">Username</label>
      <input
        type="text"
        v-model="username"
        class="input"
        placeholder="Enter username"
      />

      <!-- Password Field -->
      <label class="field-label">Password</label>
      <div class="password-field">
        <input
          :type="showPassword ? 'text' : 'password'"
          v-model="password"
          class="input"
          placeholder="Enter password"
        />
        <span class="password-toggle" @click="togglePassword">
          {{ showPassword ? '🙈' : '👁️' }}
        </span>
      </div>

      <!-- Login Button -->
      <button class="btn" @click="login">Login</button>

      <!-- Navigation to Register Page -->
      <p class="subtitle">
        Don't have an account?
        <router-link to="/register" class="link">Register here</router-link>
      </p>

      <!-- Error Message -->
      <p v-if="message" class="error-msg">
        {{ message }}
      </p>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '../axios'
import router from '../router'

const username = ref('')
const password = ref('')
const message = ref('')
const showPassword = ref(false)

function togglePassword() {
  showPassword.value = !showPassword.value
}

async function login() {
  if (!username.value || !password.value) {
    message.value = 'Please enter both username and password'
    return
  }

  try {
    const response = await api.post(`/auth/login`, {
      userName: username.value,
      password: password.value
    })

    if (response.status === 200) {
      const data = response.data  // ✅ parsed JSON automatically

      // ✅ Save user data
      sessionStorage.setItem("username", data.userName)
      sessionStorage.setItem("email", data.email)
      sessionStorage.setItem("mobile", data.mobile)
      sessionStorage.setItem("name", data.name)

      message.value = "Login Successful! Redirecting to home page..."

      setTimeout(() => {
        router.push("/home")
      }, 1500)
    }

  } catch (error) {
    message.value = error.response?.data || "Invalid username or password"
  }
}
</script>

<style scoped>
/* Theme Variables */
:root {
  --primary: #0d47a1;
  --primary-hover: #0b3e8a;
  --gradient: linear-gradient(to bottom right, #3b82f6, #1e3a8a);
  --radius: 12px;
}

/* Fix unwanted inherited body background */
:global(body) {
  background: none !important;
}

/* Fullscreen Centering + Background */
.auth-container {
  width: 100vw;
  height: 100vh;
  background: var(--gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

/* Login Card */
.auth-card {
  background: #fff;
  padding: 32px 28px;
  border-radius: var(--radius);
  width: 100%;
  max-width: 420px;
  box-shadow: 0px 12px 30px rgba(0, 0, 0, 0.18);
  animation: fadeIn .5s ease-out;
}

/* Title */
.auth-title {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 28px;
  color: var(--primary);
}

/* Label */
.field-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

/* Input */
.input {
  width: 100%;
  padding: 12px 14px;
  border-radius: var(--radius);
  border: 1.5px solid #d1d5db;
  font-size: 15px;
  margin-bottom: 16px;
  outline: none;
  transition: .25s;
}
.input:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(29, 78, 216, .25);
}

/* Password Toggle Icon */
.password-field {
  position: relative;
}
.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  cursor: pointer;
}

/* Button */
.btn {
  width: 100%;
  padding: 14px 16px;
  background: var(--primary);
  color: #fff;
  border: none;
  border-radius: var(--radius);
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: .25s;
  margin-top: 10px;
}
.btn:hover {
  background: var(--primary-hover);
  transform: translateY(-2px);
}

/* Subtitle */
.subtitle {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
}
.link {
  font-weight: 600;
  color: var(--primary);
}

/* Error Message */
.error-msg {
  margin-top: 10px;
  font-weight: 600;
  color: red;
  text-align: center;
}

/* Fade Animation */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
