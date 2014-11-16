Rails.application.routes.draw do
  root "static_pages#index"
  get "pull", to: "accounts#pull"

  namespace :api do
    namespace :v1 do
      get "search", to: "accounts#search"
    end
  end
end
