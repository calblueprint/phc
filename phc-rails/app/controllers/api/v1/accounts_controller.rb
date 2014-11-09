class Api::V1::AccountsController < ApplicationController
  respond_to :json

  def search
    if request.headers["AuthToken"].eql? ENV["AuthToken"]
      hash = { first_name: params[:first_name], last_name: params[:last_name] }
      respond_with PersonAccount.fuzzy_search(hash, false)
    else
      respond_with []
    end
  end

  def show
    respond_with PersonAccount.find(params[:id])
  end
end
