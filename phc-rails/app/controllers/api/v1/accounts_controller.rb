class Api::V1::AccountsController < ApplicationController
  respond_to :json

  def search
    respond_with params
  end

  def show
    respond_with PersonAccount.find(params[:id])
  end
end
