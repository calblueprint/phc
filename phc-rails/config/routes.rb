Rails.application.routes.draw do
  resources :accounts
  get 'pull', to: 'accounts#pull'
end
