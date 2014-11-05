class Api::V1::AccountsController < ApplicationController
  respond_to :json

  def search
    respond_with PersonAccount.fuzzy_search({first_name: params[:first_name], last_name: params[:last_name]}, false)
  end

  def show
    respond_with PersonAccount.find(params[:id])
  end
end
