Rails.application.routes.draw do
  root "static_pages#index"
  get "pull", to: "accounts#pull"

  namespace :api do
    namespace :v1 do
      get "search", to: "accounts#search"
      get "create", to: "accounts#create"
      get "check", to: "accounts#check"
    end
  end
end
