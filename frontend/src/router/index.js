import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/datasets',
      name: 'datasets',
      component: () => import('@/views/DatasetView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/parameters',
      name: 'parameters',
      component: () => import('@/views/ParameterView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/datasets',
      name: 'admin-datasets',
      component: () => import('@/views/admin/DatasetManagement.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('@/views/admin/UserManagement.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAdmin && authStore.userRole !== 'ADMIN') {
    next('/')
  } else {
    next()
  }
})

export default router