Rails.application.routes.draw do
  root "static_pages#index"

  namespace :api do
    namespace :v1 do
      get "search", to: "accounts#search"
      post "create", to: "accounts#create"
      get "check", to: "accounts#check"
      get "accounts/:sf_id", to: "accounts#show"
      get 'event_registration/create'
      get 'event_registration/search'
      get 'event_registration/update_service'
      get 'event_registration/update_feedback'
    end
  end

  resources :users

  get 'login', to:'sessions#new'
  post 'login', to:'sessions#login'
  delete 'logout', to:'sessions#destroy'

end
