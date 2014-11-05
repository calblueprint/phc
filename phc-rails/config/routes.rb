require 'api_constraints'

Rails.application.routes.draw do
  resources :accounts
  get 'pull', to: 'accounts#pull'

  namespace :api do
    namespace :v1 do
      resources :accounts
      get 'search', to: 'accounts#search'
    end
  end
end
