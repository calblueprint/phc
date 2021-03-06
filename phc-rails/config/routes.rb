Rails.application.routes.draw do
  mount RailsAdmin::Engine => '/admin', as: 'rails_admin'
  root "static_pages#index"

  namespace :api do
    namespace :v1 do
      get "search", to: "accounts#search"
      get "check", to: "accounts#check"
      get "accounts/:sf_id", to: "accounts#show"
      post "create", to: "accounts#create"

      get 'event_registrations/search'
      post 'event_registrations/create'
      get 'event_registrations/get_applied'
      post 'event_registrations/update_service'
      post 'event_registrations/update_feedback'
    end
  end

  resources :users

  get "accounts/duplicates", to: "accounts#duplicates"
  get 'login', to:'sessions#new'
  post 'login', to:'sessions#login'
  delete 'logout', to:'sessions#destroy'
  get 'services', to:'services#show'

end
