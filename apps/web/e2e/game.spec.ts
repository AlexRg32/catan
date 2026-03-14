import { expect, test } from '@playwright/test'

test('login to lobby and room flow shell', async ({ page }) => {
  await page.goto('http://localhost:5173/login')
  await expect(page.getByRole('heading', { name: 'Entrar a Catan Online' })).toBeVisible()
})
